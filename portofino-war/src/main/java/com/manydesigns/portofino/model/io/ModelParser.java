package com.manydesigns.portofino.model.io;

import com.manydesigns.elements.logging.LogUtil;
import com.manydesigns.elements.util.ReflectionUtil;
import com.manydesigns.portofino.model.Model;
import com.manydesigns.portofino.model.annotations.ModelAnnotation;
import com.manydesigns.portofino.model.datamodel.*;
import com.manydesigns.portofino.model.portlets.Portlet;
import com.manydesigns.portofino.model.site.SiteNode;
import com.manydesigns.portofino.model.usecases.UseCase;
import com.manydesigns.portofino.model.usecases.UseCaseProperty;
import com.manydesigns.portofino.xml.CharactersCallback;
import com.manydesigns.portofino.xml.DocumentCallback;
import com.manydesigns.portofino.xml.ElementCallback;
import com.manydesigns.portofino.xml.XmlParser;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Giampiero Granatella - giampiero.granatella@manydesigns.com
 * @author Angelo    Lupo       - angelo.lupo@manydesigns.com
 * @author Paolo     Predonzani - paolo.predonzani@manydesigns.com
 */
public class ModelParser extends XmlParser {
    public static final String copyright =
            "Copyright (c) 2005-2010, ManyDesigns srl";

    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------

    public static final String MODEL = "model";

    public static final String DATABASES = "databases";
    public static final String DATABASE = "database";
    public static final String SCHEMAS = "schemas";
    public static final String SCHEMA = "schema";
    public static final String TABLES = "tables";
    public static final String TABLE = "table";
    public static final String COLUMNS = "columns";
    public static final String COLUMN = "column";
    public static final String PRIMARYKEY = "primaryKey";
    public static final String PRIMARYKEYCOLUMN = COLUMN;
    public static final String FOREIGNKEYS = "foreignKeys";
    public static final String FOREIGNKEY = "foreignKey";
    public static final String REFERENCES = "references";
    public static final String REFERENCE = "reference";
    public static final String SITENODES = "siteNodes";
    public static final String SITENODE = "siteNode";
    public static final String CHILDNODES = "childNodes";
    public static final String PORTLETS = "portlets";
    public static final String PORTLET = "portlet";
    public static final String USECASES = "useCases";
    public static final String USECASE = "useCase";
    public static final String PROPERTIES = "properties";
    public static final String PROPERTY = "property";
    public static final String ANNOTATIONS = "annotations";
    public static final String ANNOTATION = "annotation";
    public static final String VALUE = "value";

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    Model model;
    Database currentDatabase;
    Schema currentSchema;
    Table currentTable;
    Column currentColumn;
    PrimaryKey currentPk;
    ForeignKey currentFk;
    UseCase currentUseCase;

    Collection<ModelAnnotation> currentModelAnnotations;
    ModelAnnotation currentModelAnnotation;


    //--------------------------------------------------------------------------
    // Logging
    //--------------------------------------------------------------------------

    public final static Logger logger = LogUtil.getLogger(ModelParser.class);

    //--------------------------------------------------------------------------
    // Constructor
    //--------------------------------------------------------------------------

    public ModelParser() {}

    //--------------------------------------------------------------------------
    // Parsing
    //--------------------------------------------------------------------------

    public Model parse(String resourceName) throws Exception {
        InputStream inputStream = ReflectionUtil.getResourceAsStream(resourceName);
        return parse(inputStream);
    }

    public Model parse(File file) throws Exception {
        LogUtil.infoMF(logger, "Parsing file: {0}", file.getAbsolutePath());
        InputStream input = new FileInputStream(file);
        return parse(input);
    }

    private Model parse(InputStream inputStream) throws XMLStreamException {
        model = new Model();
        initParser(inputStream);
        expectDocument(new ModelDocumentCallback());
        model.init();
        return model;
    }

    private class ModelDocumentCallback implements DocumentCallback {
        public void doDocument() throws XMLStreamException {
            expectElement(MODEL, 1, 1, new ModelCallback());
        }
    }

    private class ModelCallback implements ElementCallback {
        public void doElement(Map<String, String> attributes)
                throws XMLStreamException {
            expectElement(DATABASES, 0, 1, new DatabasesCallback());
            expectElement(SITENODES, 0, 1, new SiteNodesCallback());
            expectElement(PORTLETS, 0, 1, new PortletsCallback());
            expectElement(USECASES, 0, 1, new UseCasesCallback());
        }
    }

    //**************************************************************************
    // datamodel/databases
    //**************************************************************************

    private class DatabasesCallback implements ElementCallback {
        public void doElement(Map<String, String> attributes)
                throws XMLStreamException {
            expectElement(DATABASE, 1, null, new DatabaseCallback());
        }
    }

    private class DatabaseCallback implements ElementCallback {
        public void doElement(Map<String, String> attributes)
                throws XMLStreamException {
            currentDatabase = new Database();
            checkAndSetAttributes(currentDatabase, attributes);
            model.getDatabases().add(currentDatabase);
            expectElement(SCHEMAS, 1, 1, new SchemasCallback());

        }
    }

    private class SchemasCallback implements ElementCallback {
        public void doElement(Map<String, String> attributes)
                throws XMLStreamException {
            expectElement(SCHEMA, 0, null, new SchemaCallback());
        }
    }

    private class SchemaCallback implements ElementCallback {
        public void doElement(Map<String, String> attributes)
                throws XMLStreamException {
            currentSchema = new Schema(currentDatabase);
            checkAndSetAttributes(currentSchema, attributes);
            currentDatabase.getSchemas().add(currentSchema);
            expectElement(TABLES, 0, 1, new TablesCallback());
        }
    }

    private class TablesCallback implements ElementCallback {
        public void doElement(Map<String, String> attributes)
                throws XMLStreamException {
            expectElement(TABLE, 0, null, new TableCallback());
        }
    }

    private class TableCallback implements ElementCallback {
        public void doElement(Map<String, String> attributes)
                throws XMLStreamException {
            currentTable = new Table(currentSchema);
            checkAndSetAttributes(currentTable, attributes);
            currentSchema.getTables().add(currentTable);

            expectElement(COLUMNS, 1, 1, new ColumnsCallback());
            expectElement(PRIMARYKEY, 0, 1, new PrimaryKeyCallback());
            expectElement(FOREIGNKEYS, 0, 1, new ForeignKeysCallback());

            currentModelAnnotations = currentTable.getModelAnnotations();
            expectElement(ANNOTATIONS, 0, 1, new AnnotationsCallback());
        }
    }

    private class ColumnsCallback implements ElementCallback {
        public void doElement(Map<String, String> attributes)
                throws XMLStreamException {
            expectElement(COLUMN, 1, null, new ColumnCallback());
        }
    }

    private class ColumnCallback implements ElementCallback {
        public void doElement(Map<String, String> attributes)
                throws XMLStreamException {
            currentColumn = new Column(currentTable);
            checkAndSetAttributes(currentColumn, attributes);
            currentTable.getColumns().add(currentColumn);

            currentModelAnnotations = currentColumn.getModelAnnotations();
            expectElement(ANNOTATIONS, 0, 1, new AnnotationsCallback());
        }
    }

    private class PrimaryKeyCallback implements ElementCallback {
        public void doElement(Map<String, String> attributes)
                throws XMLStreamException {
            currentPk = new PrimaryKey(currentTable);
            checkAndSetAttributes(currentPk, attributes);
            currentTable.setPrimaryKey(currentPk);

            expectElement(PRIMARYKEYCOLUMN, 1, null, new PrimaryKeyColumnCallback());
        }
    }

    private class PrimaryKeyColumnCallback implements ElementCallback {
        public void doElement(Map<String, String> attributes)
                throws XMLStreamException {
            PrimaryKeyColumn pkColumn = new PrimaryKeyColumn(currentPk);
            checkAndSetAttributes(pkColumn, attributes);
            currentPk.getPrimaryKeyColumns().add(pkColumn);
        }
    }

    private class ForeignKeysCallback implements ElementCallback {
        public void doElement(Map<String, String> attributes)
                throws XMLStreamException {
            expectElement(FOREIGNKEY, 1, null, new ForeignKeyCallback());
        }
    }

    private class ForeignKeyCallback implements ElementCallback {
        public void doElement(Map<String, String> attributes)
                throws XMLStreamException {
            currentFk = new ForeignKey(currentTable);
            checkAndSetAttributes(currentFk, attributes);
            currentTable.getForeignKeys().add(currentFk);

            expectElement(REFERENCES, 1, 1, new ReferencesCallback());

            currentModelAnnotations = currentFk.getModelAnnotations();
            expectElement(ANNOTATIONS, 0, 1, new AnnotationsCallback());
        }
    }

    private class ReferencesCallback implements ElementCallback {
        public void doElement(Map<String, String> attributes)
                throws XMLStreamException {
            expectElement(REFERENCE, 1, null, new ReferenceCallback());
        }
    }

    private class ReferenceCallback implements ElementCallback {
        public void doElement(Map<String, String> attributes)
                throws XMLStreamException {
            Reference reference = new Reference(currentFk);
            checkAndSetAttributes(reference, attributes);
            currentFk.getReferences().add(reference);
        }
    }

    //**************************************************************************
    // Site nodes
    //**************************************************************************

    private class SiteNodesCallback implements ElementCallback {
        public void doElement(Map<String, String> attributes)
                throws XMLStreamException {
            expectElement(SITENODE, 1, null,
                    new SiteNodeCallback(model.getSiteNodes()));
        }
    }

    private class SiteNodeCallback implements ElementCallback {
        private final List<SiteNode> parentNodes;

        private SiteNodeCallback(List<SiteNode> parentNodes) {
            this.parentNodes = parentNodes;
        }

        public void doElement(Map<String, String> attributes)
                throws XMLStreamException {
            SiteNode currentSiteNode = new SiteNode();
            checkAndSetAttributes(currentSiteNode, attributes);
            parentNodes.add(currentSiteNode);
            expectElement(CHILDNODES, 0, 1,
                    new ChildNodesCallback(currentSiteNode.getChildNodes()));
        }
    }

    private class ChildNodesCallback implements ElementCallback {
        private final List<SiteNode> parentNodes;

        private ChildNodesCallback(List<SiteNode> parentNodes) {
            this.parentNodes = parentNodes;
        }

        public void doElement(Map<String, String> attributes)
                throws XMLStreamException {
            expectElement(SITENODE, 1, null, new SiteNodeCallback(parentNodes));
        }
    }


    //**************************************************************************
    // Portlets
    //**************************************************************************

    private class PortletsCallback implements ElementCallback {
        public void doElement(Map<String, String> attributes)
                throws XMLStreamException {
            expectElement(PORTLET, 0, null, new PortletCallback());
        }
    }

    private class PortletCallback implements ElementCallback {
        public void doElement(Map<String, String> attributes)
                throws XMLStreamException {
            Portlet portlet = new Portlet();
            checkAndSetAttributes(portlet, attributes);
            model.getPortlets().add(portlet);
        }
    }



    //**************************************************************************
    // Use cases
    //**************************************************************************

    private class UseCasesCallback implements ElementCallback {
        public void doElement(Map<String, String> attributes)
                throws XMLStreamException {
            expectElement(USECASE, 0, null, new UseCaseCallback());
        }
    }

    private class UseCaseCallback implements ElementCallback {
        public void doElement(Map<String, String> attributes)
                throws XMLStreamException {
            currentUseCase = new UseCase();
            checkAndSetAttributes(currentUseCase, attributes);
            model.getUseCases().add(currentUseCase);

            // TODO: replace this with init() code
            Table table = model.findTableByQualifiedName(
                    currentUseCase.getTable());
            currentUseCase.setActualTable(table);

            expectElement(PROPERTIES, 0, 1, new PropertiesCallback());
            
            currentModelAnnotations = currentUseCase.getAnnotations();
            expectElement(ANNOTATIONS, 0, 1, new AnnotationsCallback());
        }
    }

    private class PropertiesCallback implements ElementCallback {
        public void doElement(Map<String, String> attributes)
                throws XMLStreamException {
            expectElement(PROPERTY, 0, null, new PropertyCallback());
        }
    }

    private class PropertyCallback implements ElementCallback {
        public void doElement(Map<String, String> attributes)
                throws XMLStreamException {
            UseCaseProperty useCaseProperty = new UseCaseProperty();
            checkAndSetAttributes(useCaseProperty, attributes);
            currentUseCase.getProperties().add(useCaseProperty);

            currentModelAnnotations = useCaseProperty.getAnnotations();
            expectElement(ANNOTATIONS, 0, 1, new AnnotationsCallback());
        }
    }

    private class AnnotationsCallback implements ElementCallback {
        public void doElement(Map<String, String> attributes)
                throws XMLStreamException {
            expectElement(ANNOTATION, 0, null, new AnnotationCallback());
        }
    }

    private class AnnotationCallback implements ElementCallback {
        public void doElement(Map<String, String> attributes)
                throws XMLStreamException {
            currentModelAnnotation = new ModelAnnotation();
            checkAndSetAttributes(currentModelAnnotation, attributes);
            currentModelAnnotations.add(currentModelAnnotation);
            expectElement(VALUE, 0, null, new AnnotationValueCallback());
        }
    }

    private class AnnotationValueCallback implements ElementCallback {
        public void doElement(Map<String, String> attributes)
                throws XMLStreamException {
            expectCharacters(new AnnotationValueCharactersCallback());
        }
    }

    private class AnnotationValueCharactersCallback
            implements CharactersCallback {
        public void doCharacters(String text) throws XMLStreamException {
            currentModelAnnotation.getValues().add(text);
        }
    }

}

