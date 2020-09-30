<template>
  <div id="app">
    <router-view v-if="alive"/>
  </div>
</template>
<script>
export default {
  name: 'app',
  provide() {
    return {
      reload: this.reload
    }
  },
  data() {
    return {
      alive: true,
      activeIndex: '1',
    }
  },
  methods: {
    reload() {
      this.alive= false;
      this.$nextTick(() => {
        this.alive = true
      })
    },
    handleOpen(key, keyPath) {
      console.log(key, keyPath);
    },
    handleClose(key, keyPath) {
      console.log(key, keyPath);
    },
    handleSelect(key, keyPath) {
      console.log(key, keyPath);
    },
    //注销登录
    loginOutShow() {
      window.localStorage.clear();
      this.$router.go(0);
    },
  }
}
</script>

<style>

#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}

body {
  background-color: rgba(248, 248, 249, 0.5);
}


</style>
