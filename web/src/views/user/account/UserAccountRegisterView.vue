<template>
  <ContentField>
    <div class="row justify-content-md-center">
      <div class="col-3">
        <form @submit.prevent="register">
          <div class="mb-3">
            <label for="username" class="form-label">用户名</label>
            <input
              v-model="username"
              type="text"
              class="form-control"
              id="username"
              placeholder="请输入用户名"
            />
          </div>
          <div class="mb-3">
            <label for="password" class="form-label">密码</label>
            <input
              v-model="password"
              type="password"
              class="form-control"
              id="password"
              placeholder="请输入密码"
            />
          </div>
          <div class="mb-3">
            <label for="password" class="form-label">确认密码</label>
            <input
              v-model="confirmPassword"
              type="password"
              class="form-control"
              id="confirmPassword"
              placeholder="请再次输入密码"
            />
          </div>
          <div style="color: red;" class="error_message">{{error_message}}</div>
          <button style="width: 100%;" type="submit" class="btn btn-primary">提交</button>
        </form>
      </div>
    </div>
  </ContentField>
</template>
  
<script>
import { ref } from "vue";
import ContentField from "@/components/ContentField.vue";
import { useStore } from "vuex";

export default {
  components: {
    ContentField
  },
  setup() {
    const store = useStore();
    const username = ref("");
    const password = ref("");
    const confirmPassword = ref("");
    let error_message = ref("");

    const register = () => {
      error_message.value = "";
      store.dispatch("register", {
        username: username.value,
        password: password.value,
        confirmPassword: confirmPassword.value,
        success(resp) {
          error_message.value = resp.error_message;
        }
      });
    };
    return {
      username,
      password,
      confirmPassword,
      error_message,
      register
    };
  }
};
</script>
  
  <style>
</style>