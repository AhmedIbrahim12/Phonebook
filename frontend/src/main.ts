import Vue from 'vue'
import './plugins/axios'
import App from './App.vue'
import vuetify from './plugins/vuetify'
import CustomersService from "@/views/customers/service/CustomerService";

Vue.config.productionTip = false

export const customersService = new CustomersService();

new Vue({
    vuetify,
    render: h => h(App)
}).$mount('#app')
