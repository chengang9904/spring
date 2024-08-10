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
    // $.ajax({
    //   url: "http://127.0.0.1:3000/user/account/token/",
    //   type: "post",
    //   data: {
    //     username: "admin",
    //     password: "admin"
    //   },
    //   success(resp) {
    //     console.log(resp);
    //   },
    //   error(resp) {
    //     console.log(resp);
    //   }
    // });
    // $.ajax({
    //   url: "http://127.0.0.1:3000/user/account/token/",
    //   type: "post",
    //   data: {
    //     username: "admin",
    //     password: "admin"
    //   },
    //   success(resp) {
    //     console.log("success");
    //     console.log(resp);
    //   }
    // });

    //   $.ajax({
    //     url: "http://127.0.0.1:3000/user/account/info/",
    //     type: "get",
    //     headers: {
    //       Authorization: "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI3ZmYxNzg4ZDhlOGQ0Nzc0OGQ2NDU4OGFkODRkNzJiMiIsInN1YiI6IjIiLCJpc3MiOiJzZyIsImlhdCI6MTcyMzIxNTQ2OCwiZXhwIjoxNzIzMzAxODY4fQ.uNTaT-n1Z0E9Y7p9BmaZ6UNIytwnPHIWEQEjuQbPgBQ"
    //     },
    //     success(resp) {
    //       console.log(resp);
    //     },
    //     error(resp) {
    //       console.log(resp);
    //     }
    //   });

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
