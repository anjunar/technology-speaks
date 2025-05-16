import {AbstractSearch, Basic, Entity} from "react-ui-simplicity";
import Role from "./Role";

@Entity("credentialTableSearch")
export default class CredentialSearch extends AbstractSearch {

    $type = "credentialTableSearch"

    @Basic()
    displayName : string

    @Basic()
    roles : Role[]

}