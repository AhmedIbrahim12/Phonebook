<template>
  <div id="app">
    <v-app id="inspire">
      <data-widget></data-widget>
    </v-app>
  </div>
</template>

<script>
import CustomersWidget from '@/views/customers/components/CustomersWidget.vue'

export default {
  name: 'app',
  components: {
    'data-widget': CustomersWidget
  }
}
</script>