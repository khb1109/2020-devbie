import Vue from "vue";
import VueRouter from "vue-router";
import LoginPage from "../views/LoginPage";
import MainPage from "../views/MainPage";

Vue.use(VueRouter);

const router = new VueRouter({
  mode: "history",
  routes: [
    { path: "/", name: "home", component: MainPage },
    {
      path: "/login",
      name: "login",
      component: LoginPage
    }
  ]
});

export default router;
