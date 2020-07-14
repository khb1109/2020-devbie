<template>
  <div>
    두번째
  </div>
</template>
<script>
import axios from "axios";
export default {
  created() {
    const code = this.$route.query.code;
    axios
      .post("/api/login/oauth2/code/github", {
        code: code
      })
      .then(result => console.log(result.data));
  }
};
</script>
