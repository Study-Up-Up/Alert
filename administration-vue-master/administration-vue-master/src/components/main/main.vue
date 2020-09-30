<template>
  <div>
  <nav class="topNav">
    <el-menu
      :default-active="activeIndex"
      class="el-menu-demo"
      mode="horizontal"
      @select="handleSelect"
      background-color="#E0FFFF"
      text-color="#000000"
      active-text-color="#000000"
       router>
      <el-menu-item index="1" disabled>
        HZBY
      </el-menu-item>
      <el-menu-item index="/rule">
        首页规则
      </el-menu-item>
      <el-menu-item index="/RuleCom">
        更新规则
      </el-menu-item>
      <el-menu-item index="/RuleError">
        报警CSV
      </el-menu-item>
      <el-menu-item index="7">
        <el-link :underline="false" style="color: black" v-on:click="loginOutShow">注销登录</el-link>
      </el-menu-item>
    </el-menu>
  </nav>
    <router-view/>
    <BackTop :height="50" :bottom="50">
      <div class="top"><Icon type="ios-arrow-dropup-circle" /></div>
    </BackTop>
  </div>
</template>
<script>
export default {
  inject: ['reload'],
  data() {
    return {
      activeIndex: '1',
    }
  },
  methods: {
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


.topNav{width:100%;height:30px;position:fixed;top:0;left:0;}

body {
  background-color: rgba(248, 248, 249, 0.5);
}

.top{
  padding: 1px;
  background: black;
  color: #fff;
  text-align: center;
  border-radius: 2px;
}

</style>
