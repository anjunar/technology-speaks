import {AbstractEntity, Basic, Entity} from "react-ui-simplicity";
import User from "../control/User";
import EditorModel from "react-ui-simplicity/src/components/inputs/markdown/model/EditorModel";

@Entity("post")
export default class Post extends AbstractEntity {

    $type = "post"

    @Basic()
    user : User

    @Basic()
    editor : EditorModel

}