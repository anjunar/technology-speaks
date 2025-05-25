import {AbstractSearch, Basic, Entity} from "react-ui-simplicity";
import User from "../control/User";

@Entity("PostTableSearch")
export default class PostSearch extends AbstractSearch {

    $type = "PostTableSearch"

    @Basic()
    user : User

}