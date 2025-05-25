import {AbstractSearch, Basic, Entity} from "react-ui-simplicity";
import {LocalDate} from "@js-joda/core";

@Entity("UserTableSearch")
export default class UserSearch extends AbstractSearch {

    $type = "UserTableSearch"

    @Basic()
    email : string

    @Basic()
    name : string

    @Basic()
    birthDate : LocalDate

}