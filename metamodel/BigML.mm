
class bigml.DataModel extends bigml.BigMLElement {
    rel components : bigml.Component
    rel pipelines : bigml.Pipeline
}

class bigml.Component extends bigml.BigMLElement {
    att login : String
    att password : String
    att IPAddress : String

    rel compositeComponent : bigml.Component
    rel outputPorts : bigml.OutputPort
}

class bigml.ManagedComponent extends bigml.Component {
    rel inputPorts : bigml.InputPort
}

class bigml.OutsourcedComponent extends bigml.Component {
}

class bigml.Port extends bigml.BigMLElement {
    att format : String
    att protocol : String
    att schema : String
    att portNumber : String
}

class bigml.InputPort extends bigml.Port {
    att isMandatory : Bool
}

class bigml.OutputPort extends bigml.Port {
}

class bigml.Pipeline extends bigml.BigMLElement {
    att frequency : String
    att volume : String
    rel outputPort : bigml.OutputPort
    rel inputPort : bigml.InputPort
}

class bigml.Property  {
    att value : String
}

class bigml.BigMLElement  {
    att name : String
    rel properties : bigml.Property
}

class bigml.DataProcessor extends bigml.ManagedComponent {
}

class bigml.StorageSystem extends bigml.ManagedComponent {
    att accessRights : String
}

class bigml.DataSource extends bigml.ManagedComponent {
}

class bigml.DataVisualizer extends bigml.ManagedComponent {
}

class bigml.MessageQueue extends bigml.ManagedComponent {
}

class bigml.ControlFlowComponent extends bigml.ManagedComponent {
}
