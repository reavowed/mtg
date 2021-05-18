import {addClass} from "../../utils/element-utils";

export default function BannerText({className, ...props}) {
    return <h1 className={addClass("text-center mt-3", className)} {...props}/>;
}
