import {AbstractEntity, Basic, Entity, RootNode} from "react-ui-simplicity";
import User from "../control/User";

@Entity("Post")
export default class Post extends AbstractEntity {

    $type = "Post"

    @Basic()
    user : User

    @Basic()
    root : RootNode

}