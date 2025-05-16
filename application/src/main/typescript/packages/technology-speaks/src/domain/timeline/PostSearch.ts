import {AbstractSearch, Basic, Entity} from "react-ui-simplicity";
import User from "../control/User";

@Entity("postTableSearch")
export default class PostSearch extends AbstractSearch {

    $type = "postTableSearch"

    @Basic()
    user : User

}