<template>
  <div>
    <v-data-table
        :headers="headers"
        :items="customers"
        :options.sync="tableOptions"
        :footer-props="{itemsPerPageOptions: [5, 10, 15]}"
        :server-items-length="totalCustomers"
        :loading="loading"
        class="elevation-1"
    >
      <template v-slot:top>
        <v-toolbar flat>
          <v-toolbar-title>Customers</v-toolbar-title>
          <v-divider
              class="mx-4"
              inset
              vertical
          ></v-divider>
          <v-spacer></v-spacer>
          <v-banner>
            <v-row class="justify-end">
              <v-col cols="6">
                <v-select :items="countries"
                          placeholder="Select by country"
                          hide-details
                          outlined
                          dense
                          filled
                          clearable
                          @change="onCountryChange"
                ></v-select>
              </v-col>
              <v-col cols="6">
                <v-select :items="getValidityOptions()"
                          placeholder="Select by validity"
                          hide-details
                          outlined
                          filled
                          dense
                          clearable
                          @change="onValidityChange"
                ></v-select>
              </v-col>
            </v-row>
          </v-banner>
        </v-toolbar>
      </template>
      <template v-slot:item.valid="{ item }">
        <v-avatar :color="getColor(item.valid)" size="20"></v-avatar>
      </template>
    </v-data-table>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import TableOptions from "@/views/customers/models/TableOptions.ts";
import { customersService } from "@/main";

@Component
export default class CustomersWidget extends Vue {

  private headers: Record<string, unknown>[] = [];
  private countries = [];
  private customers = [];
  private tableOptions: TableOptions = {
    page: 1,
    itemsPerPage: 10,
    country: null,
    valid: null,
    sortBy: [''],
    sortDesc: [false]
  };
  private totalCustomers = 0;
  private loading = false;

  mounted() {
    this.createGridHeaders();
    this.loadCountries();
  }

  @Watch('tableOptions', {deep: true})
  onOptionsChanged() {
    this.loadTableData();
  }

  private createGridHeaders() {
    this.headers = [{text: 'Name', value: 'name'},
      {text: 'Phone', value: 'phone'},
      {text: 'Country', value: 'countryName', sortable: false},
      {text: 'Status', value: 'valid', sortable: false}];
  }

  private getValidityOptions() {
    return [
      {text: "Valid", value: true},
      {text: "Invalid", value: false}
    ];
  }

  private getColor(value: boolean) {
    return value ? "green" : "red";
  }

  private onValidityChange(value: boolean) {
    this.tableOptions.page = 1;
    this.tableOptions.valid = value;
  }

  private onCountryChange(value: string) {
    this.resetSortAndPagination();
    this.tableOptions.country = value;
  }

  private resetSortAndPagination() {
    this.tableOptions.page = 1;
    this.tableOptions.sortBy = [''];
    this.tableOptions.sortDesc = [false];
  }

  private loadCountries() {
    customersService.getCountries().then(resp => this.countries = resp.data);
  }

  private loadTableData() {
    this.loading = true;
    customersService.getCustomers(this.tableOptions).then(resp => {
      this.customers = resp.data.content;
      this.totalCustomers = resp.data.totalElements;
      this.loading = false;
    });
  }
}
</script>