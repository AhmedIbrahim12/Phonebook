import axios, { AxiosPromise } from 'axios';
import TableOptions from "@/views/customers/models/TableOptions";

export default class CustomersService {

    private SERVICE_URL = axios.defaults.baseURL + "/customers";

    getCustomers(tableOptions: TableOptions): AxiosPromise<any> {
        const params = {
            "page": tableOptions.page - 1,
            "size": tableOptions.itemsPerPage,
            "country": tableOptions.country,
            "valid": tableOptions.valid,
            "sortBy": tableOptions.sortBy[0],
            "sortDirection": tableOptions.sortDesc[0] ? "DESC" : "ASC"
        };
        return axios.get(this.SERVICE_URL, {params});
    }

    getCountries(): AxiosPromise<any> {
        return axios.get(this.SERVICE_URL + "/countries");
    }
}