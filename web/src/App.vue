<template>
  <NavBar />
  <RouterView />
</template>


<script>
import $ from "jquery"
import { ref } from "vue"
import NavBar from "./components/NavBar.vue";
import "bootstrap/dist/css/bootstrap.min.css"
import "bootstrap/dist/js/bootstrap"


export default {
  name: "app",
  setup() {
    let bot_name = ref('');
    let bot_rating = ref('');
    $.ajax({
      url: "http://127.0.0.1:30000/pk/getbotinfo/",
      type: "get",
      success(resp) {
        console.log(resp);
        bot_name.value = resp.name;
        bot_rating.value = resp.version;
      }
    });
    return {
      bot_name,
      bot_rating
    };
  },
  components: { NavBar }
}
</script>


<style>
body {
  background-image: url("https://cdn.acwing.com/media/article/image/2022/07/07/1_59b8f162fd-background.png");
  background-size: cover;
}
</style>
