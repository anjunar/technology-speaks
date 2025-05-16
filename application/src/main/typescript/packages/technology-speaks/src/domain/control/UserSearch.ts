import {AbstractSearch, Basic, Entity} from "react-ui-simplicity";
import {LocalDate} from "@js-joda/core";

@Entity("userTableSearch")
export default class UserSearch extends AbstractSearch {

    $type = "userTableSearch"

    @Basic()
    email : string

    @Basic()
    name : string

    @Basic()
    birthDate : LocalDate

}