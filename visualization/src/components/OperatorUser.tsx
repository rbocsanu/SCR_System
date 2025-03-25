import operatorUser from '../assets/operatoruser.png'
import '../index.css'

function OperatorUser(props) {
    return (
        <div className={'operatorUser'}>
            <div className='query' data-querytype={props.queryType}>{props.queryText}</div>
            <img src={operatorUser} alt={''}/>
        </div>
    )
}

export default OperatorUser