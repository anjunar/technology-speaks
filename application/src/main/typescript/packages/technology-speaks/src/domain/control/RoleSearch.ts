import {AbstractSearch, Basic, Entity} from "react-ui-simplicity";

@Entity("RoleTableSearch")
export default class RoleSearch extends AbstractSearch {

    $type = "RoleTableSearch"

    @Basic()
    name : string

    @Basic()
    description : string

}