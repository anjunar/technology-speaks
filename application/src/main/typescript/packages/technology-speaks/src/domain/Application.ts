import User from "./control/User";
import {ActiveObject, Basic, Entity} from "react-ui-simplicity";

@Entity("application")
export default class Application extends ActiveObject {

    $type = "application"

    @Basic()
    user: User

}