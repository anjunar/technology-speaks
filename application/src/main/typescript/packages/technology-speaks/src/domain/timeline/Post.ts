import {AbstractEntity, Basic, Entity, RootNode} from "react-ui-simplicity";
import User from "../control/User";

@Entity("post")
export default class Post extends AbstractEntity {

    $type = "post"

    @Basic()
    user : User

    @Basic()
    root : RootNode

}