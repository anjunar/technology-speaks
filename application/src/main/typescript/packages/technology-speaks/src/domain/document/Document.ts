import {AbstractEntity, Basic, Entity, MarkDown, RootNode} from "react-ui-simplicity";
import User from "../control/User";
import EditorModel from "react-ui-simplicity/src/components/inputs/markdown/model/EditorModel";

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
    editor : EditorModel

}