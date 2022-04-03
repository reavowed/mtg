import {useContext} from "preact/hooks";
import {Button} from "react-bootstrap";
import DecisionMaker from "../contexts/DecisionMaker";

export default function DecisionButton({optionToChoose, text, ...props}) {
    const decisionMaker = useContext(DecisionMaker);
    return <Button type="button"
                   className="ml-2"
                   variant="primary"
                   size="lg"
                   disabled={decisionMaker.requestInProgress}
                   onclick={!props.disabled && (() => decisionMaker.makeDecision(optionToChoose))}
                   {...props}/>;
}
