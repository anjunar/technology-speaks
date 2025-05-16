import {AbstractSearch, Basic, Entity} from "react-ui-simplicity";

@Entity("groupTableSearch")
export default class GroupSearch extends AbstractSearch {

    $type = "groupTableSearch"

    @Basic()
    name : string

}