import axios, { AxiosPromise } from 'axios';
import TableOptions from "@/views/customers/models/TableOptions";

export default class CustomersService {

    private SERVICE_URL = axios.defaults.baseURL + "/customers";

    getTableData(tableOptions: TableOptions): AxiosPromise<any> {
        const params = {
            "page": tableOptions.page - 1,
            "size": tableOptions.itemsPerPage,
            "country": tableOptions.country,
            "valid": tableOptions.valid
        };
        return axios.get(this.SERVICE_URL, {params});
    }

    getCountries(): AxiosPromise<any> {
        return axios.get(this.SERVICE_URL + "/countries");
    }
}