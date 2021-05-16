import {addClass} from "../../utils/element-utils";

export default function HorizontalCenter({className, ...props}) {
    return <div className={addClass(className, "d-flex justify-content-center")} {...props} />
}
