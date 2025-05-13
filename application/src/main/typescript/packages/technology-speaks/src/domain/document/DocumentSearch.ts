import {ActiveObject, Basic, Entity} from "react-ui-simplicity";

@Entity("documentSearch")
export default class DocumentSearch extends ActiveObject {

    $type = "documentSearch"

    @Basic()
    text : string

}