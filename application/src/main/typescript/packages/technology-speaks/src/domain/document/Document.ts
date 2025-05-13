import {AbstractEntity, Basic, Entity, RootNode} from "react-ui-simplicity";
import User from "../control/User";

@Entity("document")
export default class Document extends AbstractEntity {

    $type = "document"

    @Basic()
    score : number

    @Basic()
    title : string

    @Basic()
    user : User

    @Basic()
    root : RootNode

}