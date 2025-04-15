import User from "./user/User";
import {ActiveObject, Basic, Entity} from "react-ui-simplicity";

@Entity("Application")
export default class Application extends ActiveObject {

    @Basic()
    user: User

}