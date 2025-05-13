import GeoPoint from "./GeoPoint";
import {Basic, Entity, AbstractEntity} from "react-ui-simplicity";

@Entity("address")
export default class Address extends AbstractEntity {

    $type = "address"

    @Basic()
    street: string

    @Basic()
    number: string

    @Basic()
    zipCode: string

    @Basic()
    country: string

    @Basic()
    point : GeoPoint

}