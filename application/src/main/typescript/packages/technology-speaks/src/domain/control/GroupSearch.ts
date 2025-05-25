import {AbstractSearch, Basic, Entity} from "react-ui-simplicity";

@Entity("GroupTableSearch")
export default class GroupSearch extends AbstractSearch {

    $type = "GroupTableSearch"

    @Basic()
    name : string

}