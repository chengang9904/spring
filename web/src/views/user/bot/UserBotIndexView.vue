<template>
  <ContentField>我的Bot</ContentField>
</template>

<script>
import ContentField from "../../../components/ContentField";
import $ from "jquery";
import { useStore } from "vuex";

export default {
  components: {
    ContentField
  },
  setup() {
    const store = useStore();

    var settings = {
      url: "http://127.0.0.1:3000/user/bot/getlist/",
      method: "POST",
      timeout: 0,
      headers: {
        Authorization:
          "Bearer eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI2NmRkNmU0NzRmNGI0OTdjYWY0ZDdmOTQyMTVkNzJlOCIsInN1YiI6IjIiLCJpc3MiOiJzZyIsImlhdCI6MTcyMzM0NjI4MCwiZXhwIjoxNzIzNDMyNjgwfQ.QBYe5ZdnhqWgaHK4MEwnq0FWdKuC39roCtWpXcG9gcc"
      }
    };

    $.ajax(settings).done(function(response) {
      console.log(response);
    });

    $.ajax({
      url: "http://127.0.0.1:3000/user/bot/add/",
      type: "post",
      headers: {
        Authorization: "Bearer " + store.state.user.token
      },
      data: {
        title: "addBotTest",
        description: "Bot的描述",
        content: "addBotCode"
      },
      success(resp) {
        console.log(resp);
      }
    });
  }
};
</script>

<style scoped></style>