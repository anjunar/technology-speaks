import {AbstractEntity, ActiveObject, Basic, Entity} from "react-ui-simplicity";
import Role from "./Role";

@Entity("credential")
export default class Credential extends AbstractEntity {

    $type = "credential"

    @Basic()
    displayName : string

    @Basic()
    roles : Role[]

}