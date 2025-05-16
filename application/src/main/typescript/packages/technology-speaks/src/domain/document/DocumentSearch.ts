import {Basic, Entity} from "react-ui-simplicity";
import {AbstractSearch} from "react-ui-simplicity";

@Entity("documentTableSearch")
export default class DocumentSearch extends AbstractSearch {

    $type = "documentTableSearch"

    @Basic()
    text : string

}