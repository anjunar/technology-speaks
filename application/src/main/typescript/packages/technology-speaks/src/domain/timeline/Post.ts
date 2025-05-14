import {AbstractEntity, Basic, EditorModel, Entity} from "react-ui-simplicity";
import User from "../control/User";

@Entity("post")
export default class Post extends AbstractEntity {

    $type = "post"

    @Basic()
    user : User

    @Basic()
    editor : EditorModel

}