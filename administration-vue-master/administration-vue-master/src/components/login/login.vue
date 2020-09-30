<template>
  <div>
  <h1 style="font-style: italic" class="text-center">涵智博雅规则管理系统&emsp;<small>公司-涵智博雅</small></h1>
    <br>
  <Form ref="formInline" :model="formInline" :rules="ruleInline" inline>
    <FormItem prop="username">
      <label>
        <Input type="text" v-model="formInline.username" placeholder="用户名">
          <Icon type="ios-person-outline" slot="prepend"></Icon>
        </Input>
      </label>
    </FormItem>
    <FormItem prop="password">
      <label>
        <Input type="password" v-model="formInline.password" placeholder="密码">
          <Icon type="ios-lock-outline" slot="prepend"></Icon>
        </Input>
      </label>
    </FormItem>
    <FormItem>
      <Button type="primary" @click="handleSubmit('formInline')" v-preventReClick>登录</Button>
    </FormItem>
  </Form>
  </div>
</template>
<script>
export default {
  data () {
    return {
      formInline: {
        username: '',
        password: '',
      },
     ruleInline: {
        username: [
          { required: true, message: '用户名不可以为空', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '密码不可以为空', trigger: 'blur' },
          { type: 'string', min: 4, message: '密码长度不符合规则', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    handleSubmit(name) {
      this.$refs[name].validate((valid) => {
        if (valid) {
          this.$http.post('/vue/loginToken', {
            username: this.formInline.username,
            password: this.formInline.password,
          }).then(res => {
            console.log(res);
            if (res.data.code === 200) {
              //保存Token
              window.localStorage.setItem('token',res.data.data.token);
              this.$router.replace({name: 'Main'});
              this.$router.go();
            }else {
              alert(res.data.data.message);
            }
          })
        } else {
          this.$Message.error('输入不符合规则');
        }
      })
    }
  }
}
</script>

