<template>
  <div style="margin-top: 20px;" class="container">
    <div class="row">
      <div class="col-3">
        <div class="card">
          <div class="card-body">
            <img :src="$store.state.user.photo" alt="头像" style="width: 100%;" />
          </div>
        </div>
      </div>
      <div class="col-9">
        <div class="card" style="padding: 10px;">
          <div class="card-heaer">
            <span style="font-size: 150%;">我的bot</span>
            <button
              type="button"
              class="btn btn-primary float-end"
              data-bs-toggle="modal"
              data-bs-target="#addBotButton"
            >创建Bot</button>

            <!-- Modal addBot -->
            <div
              class="modal fade"
              id="addBotButton"
              tabindex="-1"
              aria-labelledby="exampleModalLabel"
              aria-hidden="true"
            >
              <div class="modal-dialog modal-xl">
                <div class="modal-content">
                  <div class="modal-header">
                    <h1 class="modal-title fs-5" id="exampleModalLabel">创建Bot</h1>
                    <button
                      type="button"
                      class="btn-close"
                      data-bs-dismiss="modal"
                      aria-label="Close"
                    ></button>
                  </div>
                  <div class="modal-body">
                    <form>
                      <div class="mb-3">
                        <label for="add-bot-title" class="form-label">名字</label>
                        <input
                          v-model="botadd.title"
                          type="text"
                          placeholder="请输入Bot名字"
                          class="form-control"
                          id="add-bot-title"
                        />
                      </div>
                      <div class="mb-3">
                        <label for="add-bot-description" class="form-label">描述</label>
                        <textarea
                          v-model="botadd.description"
                          type="text"
                          placeholder="请输入Bot描述"
                          class="form-control"
                          id="add-bot-description"
                          rows="3"
                        />
                      </div>
                      <div class="mb-3">
                        <label for="add-bot-content" class="form-label">代码</label>
                        <textarea
                          v-model="botadd.content"
                          type="text"
                          placeholder="请输入Bot代码"
                          class="form-control"
                          id="add-bot-content"
                          rows="10"
                        />
                      </div>
                    </form>
                  </div>
                  <div class="modal-footer">
                    <span style="color: red;" class="error_message">{{botadd.error_message}}</span>
                    <button @click="add_bot" type="button" class="btn btn-primary">创建</button>
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="card-body">
            <table class="table table-striped table-hover">
              <thead>
                <tr>
                  <th scope="col">名称</th>
                  <th scope="col">创建时间</th>
                  <th scope="col">天梯</th>
                  <th scope="col">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="bot in bots" :key="bot.id">
                  <td>{{ bot.title }}</td>
                  <td>{{ bot.createtime }}</td>
                  <td>{{ bot.rating }}</td>
                  <td>
                    <button
                      data-bs-toggle="modal"
                      :data-bs-target="'#modifyBotButton' + bot.id"
                      type="button"
                      class="btn btn-secondary"
                    >修改</button>

                    <!-- Modal modifyBot -->
                    <div
                      class="modal fade"
                      :id="'modifyBotButton' + bot.id"
                      tabindex="-1"
                      aria-labelledby="exampleModalLabel"
                      aria-hidden="true"
                    >
                      <div class="modal-dialog modal-xl">
                        <div class="modal-content">
                          <div class="modal-header">
                            <h1 class="modal-title fs-5" id="exampleModalLabel">修改Bot</h1>
                            <button
                              type="button"
                              class="btn-close"
                              data-bs-dismiss="modal"
                              aria-label="Close"
                            ></button>
                          </div>
                          <div class="modal-body">
                            <form>
                              <div class="mb-3">
                                <label for="add-bot-title" class="form-label">名字</label>
                                <input
                                  v-model="botmodify.title"
                                  type="text"
                                  placeholder="请输入Bot名字"
                                  class="form-control"
                                  id="add-bot-title"
                                />
                              </div>
                              <div class="mb-3">
                                <label for="add-bot-description" class="form-label">描述</label>
                                <textarea
                                  v-model="botmodify.description"
                                  type="text"
                                  placeholder="请输入Bot描述"
                                  class="form-control"
                                  id="add-bot-description"
                                  rows="3"
                                />
                              </div>
                              <div class="mb-3">
                                <label for="add-bot-content" class="form-label">代码</label>
                                <textarea
                                  v-model="botmodify.content"
                                  type="text"
                                  placeholder="请输入Bot代码"
                                  class="form-control"
                                  id="add-bot-content"
                                  rows="10"
                                />
                              </div>
                            </form>
                          </div>
                          <div class="modal-footer">
                            <span
                              style="color: red;"
                              class="error_message"
                            >{{botmodify.error_message}}</span>
                            <button
                              @click="modify_bot(bot.id)"
                              type="button"
                              class="btn btn-primary"
                            >修改</button>
                            <button
                              type="button"
                              class="btn btn-secondary"
                              data-bs-dismiss="modal"
                            >取消</button>
                          </div>
                        </div>
                      </div>
                    </div>

                    <button
                      @click="remove_bot(bot.id)"
                      style="margin-left: 10px;"
                      type="button"
                      class="btn btn-danger"
                    >删除</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useStore } from "vuex";
import { ref, reactive } from "vue";
import $ from "jquery";
import { Modal } from "bootstrap/dist/js/bootstrap";

export default {
  components: {},
  setup() {
    const botadd = reactive({
      title: "",
      description: "",
      content: "",
      error_message: ""
    });
    const botmodify = reactive({
      title: "",
      description: "",
      content: "",
      error_message: ""
    });
    const store = useStore();
    let bots = ref([]);

    const add_bot = () => {
      botadd.error_message = "";
      $.ajax({
        url: "http://localhost:3000/user/bot/add/",
        type: "post",
        headers: {
          Authorization: "Bearer " + store.state.user.token
        },
        data: {
          title: botadd.title,
          description: botadd.description,
          content: botadd.content
        },
        success(resp) {
          if (resp.error_message === "success") {
            botadd.title = "";
            botadd.description = "";
            botadd.content = "";
            Modal.getInstance("#addBotButton").hide();
            refresh_bots();
          } else {
            botadd.error_message = resp.error_message;
          }
        },
        error(resp) {
          console.log(resp);
        }
      });
    };

    const k = ref("");
    const refresh_bots = () => {
      $.ajax({
        url: "http://localhost:3000/user/bot/getlist/",
        type: "get",
        headers: {
          Authorization: "Bearer " + store.state.user.token
        },
        success(resp) {
          bots.value = resp;
          console.log(resp);
        },
        error(resp) {
          console.log(resp);
        }
      });
    };

    const remove_bot = id => {
      console.log("remove", id);
      $.ajax({
        url: "http://localhost:3000/user/bot/remove/",
        type: "post",
        headers: {
          Authorization: "Bearer " + store.state.user.token
        },
        data: {
          bot_id: id
        },
        success(resp) {
          console.log(resp);
          refresh_bots();
        }
      });
    };

    const modify_bot = id => {
      botmodify.error_message = "";
      $.ajax({
        url: "http://localhost:3000/user/bot/update/",
        type: "post",
        headers: {
          Authorization: "Bearer " + store.state.user.token
        },
        data: {
          bot_id: id,
          title: botmodify.title,
          description: botmodify.description,
          content: botmodify.content
        },
        success(resp) {
          if (resp.error_message === "success") {
            botmodify.title = "";
            botmodify.description = "";
            botmodify.content = "";
            Modal.getInstance("#modifyBotButton" + id).hide();
            refresh_bots();
          } else {
            botmodify.error_message = resp.error_message;
          }
        }
      });
    };

    refresh_bots();

    return {
      bots,
      k,
      refresh_bots,
      botadd,
      botmodify,
      add_bot,
      remove_bot,
      modify_bot
    };
  }
};
</script>

<style scoped>
</style>