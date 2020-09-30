import Vue from 'vue'
import Router from 'vue-router'
import Login from "@/components/login/login"
import Rule from "@/components/rule/rule"
import Main from "@/components/main/main"
import RuleCom from "@/components/rule/ruleCombination"
import RuleError from "@/components/rule/ruleError"



Vue.use(Router)

export default new Router({
  //HTML的路由模式，可以不使用。
  mode: 'history',
  routes: [
    {
      redirect: '/rule',
      path: '/',
      meta: {
        title: '规则管理系统主页面'
      },name: 'Main',
      component: Main,
      children: [{
        path: 'rule',
        meta: {
          title: '规则管理修改规则'
        },
        name: 'Rule',
        component: Rule,
      },{
        path: 'ruleCom',
        meta: {
          title: '组合规则'
        },
        name: 'RuleCom',
        component: RuleCom,
      },{
          path: 'ruleError',
          meta: {
            title: '规则管理错误消息'
          },
          name: 'RuleError',
          component: RuleError
        }]
    },
    {
      path: '/login',
      meta: {
        title: '规则管理系统登录页面'
      },
      name: 'Login',
      component: Login
    }
  ]
})
