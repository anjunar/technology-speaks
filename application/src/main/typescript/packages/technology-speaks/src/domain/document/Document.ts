import {AbstractEntity, Basic, Entity, RootNode} from "react-ui-simplicity";
import User from "../control/User";

@Entity("Document")
export default class Document extends AbstractEntity {

    $type = "Document"

    @Basic()
    score : number

    @Basic()
    title : string

    @Basic()
    user : User

    @Basic()
    root : RootNode

}