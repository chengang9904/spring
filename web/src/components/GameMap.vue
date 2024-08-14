<template>
  <div ref="parent" class="gamemap">
    <canvas ref="canvas" tabindex="0"></canvas>
  </div>
</template>

<script>
import { ref, onMounted } from "vue";
import { GameMap } from "@/assets/scripts/GameMap";
import { useStore } from "vuex";

export default {
  name: "GameMap",
  setup() {
    const parent = ref(null);
    const canvas = ref(null);
    const store = useStore();

    onMounted(() => {
      store.commit(
        "updateGameObject",
        new GameMap(canvas.value.getContext("2d"), parent.value, store)
      );
    });

    return {
      parent,
      canvas,
    };
  },
};
</script>
<style>
.gamemap {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>