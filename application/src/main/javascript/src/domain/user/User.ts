import Identity from "./Identity";
import UserInfo from "./UserInfo";
import Role from "./Role";
import Address from "./Address";
import {Basic, Entity} from "react-ui-simplicity";
import EMail from "./EMail";

@Entity("User")
export default class User extends Identity {

    $type = "User"

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


}