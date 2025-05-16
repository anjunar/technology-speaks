import Identity from "./Identity";
import UserInfo from "./UserInfo";
import Role from "./Role";
import Address from "./Address";
import {Basic, Entity} from "react-ui-simplicity";
import EMail from "./EMail";

@Entity("user")
export default class User extends Identity {

    $type = "user"

    @Basic()
    name : string

    @Basic()
    nickName : string

    @Basic()
    emails : EMail[] = []

    @Basic()
    password : string = ""

    @Basic()
    info : UserInfo

    @Basic()
    address : Address

    @Basic()
    roles : Role[] = []

    @Basic()
    score : number


}