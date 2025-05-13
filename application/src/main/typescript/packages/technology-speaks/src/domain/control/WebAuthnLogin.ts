import {ActiveObject, Basic, Entity} from "react-ui-simplicity";

@Entity("webAuthnLogin")
export default class WebAuthnLogin extends ActiveObject {

    $type = "webAuthnLogin"

    @Basic()
    username : string

    @Basic()
    displayName : string

}