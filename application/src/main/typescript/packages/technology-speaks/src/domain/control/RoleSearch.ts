import {AbstractSearch, Basic, Entity} from "react-ui-simplicity";

@Entity("roleTableSearch")
export default class RoleSearch extends AbstractSearch {

    $type = "roleTableSearch"

    @Basic()
    name : string

    @Basic()
    description : string

}