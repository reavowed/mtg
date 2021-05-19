import {useContext} from "preact/hooks";
import DecisionMaker from "../../DecisionMaker";

export default function DecisionButton({optionToChoose, text, ...props}) {
    const decisionMaker = useContext(DecisionMaker);
    return <button type="button"
                   className="btn btn-primary btn-lg ml-2"
                   disabled={decisionMaker.requestInProgress}
                   onclick={!props.disabled && (() => decisionMaker.makeDecision(optionToChoose))}
                   {...props}/>;
}
