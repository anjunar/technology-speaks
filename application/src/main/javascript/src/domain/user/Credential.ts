import {ActiveObject, Basic, Entity} from "react-ui-simplicity";
import Role from "./Role";

@Entity("Credential")
export default class Credential extends ActiveObject {

    $type = "Credential"

    @Basic()
    displayName : string

    @Basic()
    roles : Role[]

}