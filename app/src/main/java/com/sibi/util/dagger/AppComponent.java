package com.sibi.util.dagger;

import com.sibi.Sibi;
import com.sibi.SplashActivity;
import com.sibi.YoutubeActivity;
import com.sibi.authentication.view.LoginFragment;
import com.sibi.authentication.view.RegistrationFragment;
import com.sibi.primary.MainActivity;
import com.sibi.primary.ManageBudgetFragment;
import com.sibi.primary.category.view.EditCategoryFragment;
import com.sibi.primary.dashboard.view.DashboardFragment;
import com.sibi.primary.drawer.view.NavDrawerFragment;
import com.sibi.primary.editTransaction.view.EditTransactionFragment;
import com.sibi.primary.reviewTransactions.view.CategoryFragment;
import com.sibi.util.LogActivity;
import com.sibi.welcome.WelcomeActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by adway on 28/11/17.
 */

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(LoginFragment loginFragment);
    void inject(RegistrationFragment registrationFragment);
    void inject(LogActivity logActivity);
    void inject(SplashActivity splashActivity);
    void inject(Sibi sibi);
    void inject(DashboardFragment dashboardFragment);
    void inject(MainActivity mainActivity);
    void inject(CategoryFragment categoryFragment);
    void inject(NavDrawerFragment navDrawerFragment);
    void inject(EditTransactionFragment editTransactionFragment);
    void inject(EditCategoryFragment editCategoryFragment);
    void inject(ManageBudgetFragment manageBudgetFragment);
    void inject(YoutubeActivity youtubeActivity);
    void inject(WelcomeActivity welcomeActivity);

}
