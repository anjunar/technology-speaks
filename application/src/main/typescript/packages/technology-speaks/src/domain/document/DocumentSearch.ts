import {ActiveObject, Basic, Entity} from "react-ui-simplicity";

@Entity("DocumentSearch")
export default class DocumentSearch extends ActiveObject {

    $type = "DocumentSearch"

    @Basic()
    text : string

}