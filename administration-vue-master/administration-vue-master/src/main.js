// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import Alert from './api/Alert'
import axios from "./api/axios"
import ViewUI from 'view-design'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import 'view-design/dist/styles/iview.css';
import plugins from './api/plugins'

Vue.config.productionTip = false
Vue.prototype.$http=axios;
Vue.use(Alert);
Vue.use(ViewUI);
Vue.use(ElementUI);
Vue.use(plugins)

//钩子方法验证Token是否存在
router.beforeEach((to, from, next) => {
  //改变网页的标题
  window.document.title = to.meta.title;
  //强制登录并且检测token
  let token = localStorage.getItem('token');
  if (!token && to.path !== '/login') {
    return next('/login')
  }
  ViewUI.LoadingBar.start();
  next();
});

//返回页面的顶端
router.afterEach(route => {
  window.scrollTo(0,0);
  ViewUI.LoadingBar.finish();
});


/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>'
})
