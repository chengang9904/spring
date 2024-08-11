<template>
  <NavBar />
  <RouterView></RouterView>
</template>


<script>
import NavBar from "./components/NavBar.vue";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap";

export default {
  name: "app",
  setup() {
    return {};
  },
  components: { NavBar }
};
</script>


<style>
body {
  background-image: url("https://cdn.acwing.com/media/article/image/2022/07/07/1_59b8f162fd-background.png");
  background-size: cover;
}
</style>