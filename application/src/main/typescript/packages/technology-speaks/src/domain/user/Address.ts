import GeoPoint from "./GeoPoint";
import AbstractEntity from "../core/AbstractEntity";
import {Basic, Entity} from "react-ui-simplicity";

@Entity("Address")
export default class Address extends AbstractEntity {

    $type = "Address"

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