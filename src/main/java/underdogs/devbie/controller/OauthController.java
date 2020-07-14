package underdogs.devbie.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import underdogs.devbie.domain.User;
import underdogs.devbie.domain.UserRepository;

@Controller
public class OauthController {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private HttpComponentsClientHttpRequestFactory factory;

	@Value("${oauth2.github.client-id}")
	private String clientId;

	@Value("${oauth2.github.client-secret}")
	private String clientSecret;

	@GetMapping("/oauth2/authorization/github")
	public String login() {
		return "redirect:https://github.com/login/oauth/authorize?"
			+ "client_id=" + clientId + "&"
			+ "redirect_uri=http://localhost:8080/login";
	}

	@PostMapping("/api/login/oauth2/code/github")
	public ResponseEntity callBack(@RequestBody Map<String, String> paramMap) {
		String token = findAccessToken(paramMap.get("code"));
		Map<String, Object> userInfo = findUser(token);

		// DB에 User 등록
		User user = User.builder()
			.name((String)userInfo.get("name"))
			.email((String)userInfo.get("email"))
			.build();

		User persistentUser = userRepository.save(user);
		// 사용자에게 localstorage jwt set

		return ResponseEntity.ok(persistentUser);
	}

	private String findAccessToken(String code) {
		RestTemplate restTemplate = new RestTemplate(factory);

		String requestJson = "{\"client_id\":\"e71a1231ff16a3b25f07\","
			+ "\"client_secret\":\"6f1c84b543335c4bc13233829fcc6fe2bbf461bb\","
			+ "\"code\":\"" + code + "\"}";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

		try {
			Map<String, String> result = restTemplate.postForObject(
				"https://github.com/login/oauth/access_token",
				entity,
				Map.class);
			return result.get("access_token");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new IllegalArgumentException();
		}
	}

	private Map<String, Object> findUser(String token) {
		RestTemplate restTemplate = new RestTemplate(factory);

		restTemplate.getInterceptors().add((request, body, execution) -> {
			request.getHeaders().setBearerAuth(token);
			return execution.execute(request, body);
		});

		try {
			return restTemplate.getForObject("https://api.github.com/user", Map.class);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new IllegalArgumentException();
		}
	}
}
