package dbfit.fixture;

import dbfit.api.DBEnvironment;
import dbfit.api.DbEnvironmentFactory;
import dbfit.util.NameNormaliser;
import dbfit.util.Options;
import static dbfit.util.Options.OPTION_AUTO_COMMIT;

import fit.Fixture;
import fit.Parse;

import java.sql.SQLException;

public class SetOption extends Fixture {
    private DBEnvironment dbEnvironment;
    private String option;
    private String value;

    public SetOption() {
        dbEnvironment = DbEnvironmentFactory.getDefaultEnvironment();
    }

    public SetOption(DBEnvironment env, String option, String value) {
        this.dbEnvironment = env;
        this.option = NameNormaliser.normaliseName(option);
        this.value = value;
    }

    public void setOption() throws SQLException {
        Options.setOption(getOption(), getValue());
        setEnvironmentOption();
    }

    @Override
    public void doRows(Parse rows) {
        try {
            setOption();
        } catch (Throwable e) {
            throw new Error(e);
        }
    }

    private String getOption() {
        if (option == null) {
            option = NameNormaliser.normaliseName(args[0]);
        }
        return option;
    }

    private String getValue() {
        if (value == null) {
            value = args[1];
        }
        return value;
    }

    private void setEnvironmentOption() throws SQLException {
        if (OPTION_AUTO_COMMIT.equals(getOption()) && dbEnvironment != null) {
            dbEnvironment.setAutoCommit();
        }
    }
}
