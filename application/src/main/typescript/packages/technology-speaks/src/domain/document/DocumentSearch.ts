import {Basic, Entity} from "react-ui-simplicity";
import {AbstractSearch} from "react-ui-simplicity";

@Entity("DocumentTableSearch")
export default class DocumentSearch extends AbstractSearch {

    $type = "DocumentTableSearch"

    @Basic()
    text : string

}